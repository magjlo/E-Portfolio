#include <stdio.h>
#include <stdlib.h>
#define COLUMNS 6
int gloind = 0;

void print_number(int n){               
  if(gloind == COLUMNS){                //If gloind has reached beyond last index (5) for COLUMNS --> 
    gloind = 1;                         //sets to 1 and prints on new line
    printf("\n");
    printf("%10d ", n);
  }
  else{
    printf("%10d ", n);                 //For COLUMNS indexed 0,1,2,3,4,5 prints given number
    gloind++;
  }
}

void print_sieves(int n){
  // Should print out all prime numbers less than 'n'
  // with the following formatting. Note that
  // the number of columns is stated in the define
  // COLUMNS
    int* pnumarr; //allocates a pointer pnumarr
    pnumarr = (int*) malloc(n* sizeof(int));  //reserves memory to given address of pointer
    for(int i = 0; i < n; i++){ //set all elements in array to 1-->
        pnumarr[i] = 1; //-->
    }
    for(int i = 2; i*i <= n; i++){  //for i = 2 and not exceeding square root of n
      if(pnumarr[i] == 1){          //if element i = 1
        for(int j = i*i; j <= n; j = j + i){  // all elements = i^2 + x*i, where x is number of loops passed-->, (first loop = i^2)
          pnumarr[j] = 0; //are set to 0, these elements are multiplicants of prime numbers, j cannot exceed n (given parameter)
        }           
      }
    } 
    for(int i = 2; i <= n; i++){  //prints all indexes in array from index 2 to n, remember number 2 is on pnumarr[2]
      if(pnumarr[i] == 1){  //checks if position of any given index is a prime (= 1)
        print_number(i);  //prints prime
      }
    } 
    free(pnumarr);  //frees memory which is no longer needed
}

// 'argc' contains the number of program arguments, and
// 'argv' is an array of char pointers, where each
// char pointer points to a null-terminated string.
int main(int argc, char *argv[]){
  if(argc == 2){
    print_sieves(atoi(argv[1]));
  }
  else
    printf("Please state an interger number.\n");
    
  return 0;
}