#include <stdio.h>
int main(void)
{
    int n;
    scanf("%d", &n);
    printf("%s\n", (n<3) ? "NO" : ((n-2)%2==0) ? "YES" : "NO");
    return 0;
}
