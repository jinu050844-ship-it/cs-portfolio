#define _XOPEN_SOURCE 700
#include <errno.h>
#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>

static int is_space(char c) {
    return (c==' ' || c=='\n' || c=='\t' || c=='\r' || c=='\f' || c=='\v');
}

static int isPrime_ll(long long n) {
    if (n < 2) return 0;
    if (n == 2) return 1;
    if ((n & 1LL) == 0LL) return 0;
    for (long long d = 3; d <= n / d; d += 2) {
        if (n % d == 0) return 0;
    }
    return 1;
}

static void die(const char *msg) {
    perror(msg);
    exit(1);
}

static void build_ranges_by_delim(const char *filename, int nproc, off_t *start, off_t *end) {
    struct stat st;
    if (stat(filename, &st) < 0) die("stat");
    off_t filesize = st.st_size;

    off_t chunk = (nproc > 0) ? (filesize / nproc) : filesize;

    for (int i = 0; i < nproc; i++) {
        start[i] = (off_t)i * chunk;
        end[i]   = (i == nproc - 1) ? filesize : (off_t)(i + 1) * chunk;
    }

    int fd = open(filename, O_RDONLY);
    if (fd < 0) die("open(range)");

    for (int i = 0; i < nproc - 1; i++) {
        off_t pos = end[i];
        if (pos < 0) pos = 0;
        if (pos > filesize) pos = filesize;

        char c;
        while (pos < filesize) {
            ssize_t r = pread(fd, &c, 1, pos);
            if (r != 1) break;
            if (is_space(c)) {
                pos++;
                while (pos < filesize) {
                    r = pread(fd, &c, 1, pos);
                    if (r != 1) break;
                    if (!is_space(c)) break;
                    pos++;
                }
                break;
            }
            pos++;
        }

        end[i] = pos;
        start[i + 1] = pos;
    }

    close(fd);
}

static long count_primes_in_range(const char *filename, off_t s, off_t e) {
    if (e <= s) return 0;

    int fd = open(filename, O_RDONLY);
    if (fd < 0) {
        perror("open(child)");
        _exit(1);
    }

    off_t len = e - s;
    if (len <= 0) {
        close(fd);
        return 0;
    }

    char *buf = (char *)malloc((size_t)len + 1);
    if (!buf) {
        perror("malloc");
        close(fd);
        _exit(1);
    }

    off_t off = 0;
    while (off < len) {
        ssize_t r = pread(fd, buf + off, (size_t)(len - off), s + off);
        if (r < 0) {
            if (errno == EINTR) continue;
            perror("pread(child)");
            free(buf);
            close(fd);
            _exit(1);
        }
        if (r == 0) break;
        off += r;
    }
    buf[off] = '\0';

    long cnt = 0;
    char *saveptr = NULL;
    char *tok = strtok_r(buf, " \n\t\r\f\v", &saveptr);
    while (tok) {
        char *endp = NULL;
        errno = 0;
        long long v = strtoll(tok, &endp, 10);
        if (errno == 0 && endp && *endp == '\0') {
            if (isPrime_ll(v)) cnt++;
        }
        tok = strtok_r(NULL, " \n\t\r\f\v", &saveptr);
    }

    free(buf);
    close(fd);
    return cnt;
}

int main(int argc, char *argv[]) {
    if (argc != 3) {
        fprintf(stderr, "usage: %s <filename> <nproc>\n", argv[0]);
        return 1;
    }

    const char *filename = argv[1];
    int nproc = atoi(argv[2]);
    if (nproc <= 0) {
        fprintf(stderr, "nproc must be > 0\n");
        return 1;
    }

    off_t *start = (off_t *)calloc((size_t)nproc, sizeof(off_t));
    off_t *end   = (off_t *)calloc((size_t)nproc, sizeof(off_t));
    if (!start || !end) die("calloc");

    build_ranges_by_delim(filename, nproc, start, end);

    int pipefd[2];
    if (pipe(pipefd) < 0) die("pipe");

    for (int i = 0; i < nproc; i++) {
        pid_t pid = fork();
        if (pid < 0) die("fork");

        if (pid == 0) {
            close(pipefd[0]);

            off_t s = start[i];
            off_t e = end[i];

            long primes = count_primes_in_range(filename, s, e);

            printf("[child %d] offset [%lld, %lld) primes=%ld\n",
                   (int)getpid(), (long long)s, (long long)e, primes);
            fflush(stdout);

            if (write(pipefd[1], &primes, sizeof(primes)) != (ssize_t)sizeof(primes)) {
                perror("write(pipe)");
            }

            close(pipefd[1]);
            _exit(0);
        }
    }

    close(pipefd[1]);

    long total = 0;
    for (int i = 0; i < nproc; i++) {
        long x = 0;
        ssize_t r;
        do {
            r = read(pipefd[0], &x, sizeof(x));
        } while (r < 0 && errno == EINTR);

        if (r == (ssize_t)sizeof(x)) total += x;
        else if (r < 0) die("read(pipe)");
        else {
        }
    }
    close(pipefd[0]);

    while (1) {
        pid_t w = wait(NULL);
        if (w < 0) {
            if (errno == ECHILD) break;
            die("wait");
        }
    }

    printf("[parent %d] total primes=%ld\n", (int)getpid(), total);
    fflush(stdout);

    free(start);
    free(end);
    return 0;
}
