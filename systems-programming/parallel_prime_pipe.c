#define _XOPEN_SOURCE 700
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/wait.h>
#include <errno.h>

static int isPrime(long n) {
    if (n < 2) return 0;
    if (n == 2) return 1;
    if ((n & 1L) == 0L) return 0;
    for (long d = 3; d * d <= n; d += 2)
        if (n % d == 0) return 0;
    return 1;
}

static inline int is_space(char c) {
    return (c==' ' || c=='\n' || c=='\t' || c=='\r' || c=='\f' || c=='\v');
}

int main(int argc, char *argv[]) {
    if (argc != 3) {
        fprintf(stderr, "Usage: %s filename nproc\n", argv[0]);
        exit(1);
    }

    char *filename = argv[1];
    int nproc = atoi(argv[2]);
    if (nproc <= 0) {
        fprintf(stderr, "nproc must be positive\n");
        exit(1);
    }

    struct stat st;
    if (stat(filename, &st) < 0) { perror("stat"); exit(1); }
    off_t filesize = st.st_size;
    off_t chunk = filesize / nproc;

    int fd = open(filename, O_RDONLY);
    if (fd < 0) { perror("open"); exit(1); }

    off_t start[nproc], end[nproc];

    for (int i = 0; i < nproc; i++) {
        start[i] = i * chunk;
        end[i]   = (i == nproc - 1) ? filesize : (i + 1) * chunk;
    }

    for (int i = 0; i < nproc - 1; i++) {
        off_t pos = end[i];
        char c;
        while (pos < filesize) {
            if (pread(fd, &c, 1, pos) != 1) break;
            if (is_space(c)) { pos++; break; }
            pos++;
        }
        end[i] = pos;
        start[i+1] = pos;
    }

    close(fd);

    int pipefd[2];
    if (pipe(pipefd) < 0) { perror("pipe"); exit(1); }

    for (int i = 0; i < nproc; i++) {
        pid_t pid = fork();
        if (pid < 0) { perror("fork"); exit(1); }

        if (pid == 0) {
            close(pipefd[0]);

            off_t s = start[i];
            off_t e = end[i];

            int cfd = open(filename, O_RDONLY);
            if (cfd < 0) { perror("open child"); exit(1); }

            long count = 0;
            off_t len = e - s;
            if (len > 0) {
                char *buf = malloc(len+1);
                lseek(cfd, s, SEEK_SET);
                ssize_t rd = read(cfd, buf, len);
                buf[rd] = '\0';

                char *p, *save;
                p = strtok_r(buf, " \n\t\r\f\v", &save);
                while (p) {
                    long v = atol(p);
                    if (isPrime(v)) count++;
                    p = strtok_r(NULL, " \n\t\r\f\v", &save);
                }
                free(buf);
            }

            printf("[child %d] offset [%lld, %lld) primes=%ld\n",
                   getpid(), (long long)s, (long long)e, count);
            fflush(stdout);

            write(pipefd[1], &count, sizeof(long));
            close(pipefd[1]);
            close(cfd);
            exit(0);
        }
    }

    close(pipefd[1]);

    long total = 0;
    long val;

    while (read(pipefd[0], &val, sizeof(long)) > 0) {
        printf("[parent %d] read value is %ld.\n", getpid(), val);
        fflush(stdout);
        total += val;
    }

    close(pipefd[0]);

    while (wait(NULL) > 0);

    printf("[parent %d] Found %ld prime numbers in total.\n",
           getpid(), total);
    fflush(stdout);

    return 0;
}

