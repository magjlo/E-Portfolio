
//Simple non-dynamic stack implementation in C
typedef struct stack {
    int top;
    int size;
    int* array;
}   stack;

stack* new_stack(int size) {
    int* array = (int*)malloc(size*sizeof(int));
    stack* stk = (stack*)malloc(sizeof(stack));
    stk->size = size;
    stk->array = array;
    stk->top = 0;
    return stk;
}

void push(stack* stk, int value){
    if(stk->top == stk->size){
        printf("Stack full.\n"); 
        return -1;
    }
    stk->array[stk->top] = value;
    stk->top += 1;
}

int pop(stack* stk) {
    if(stk->top == 0){
        printf("Empty.\n"); 
        return -1;
    }
    stk->top -= 1;
    int va = stk->array[stk->top];
    return va;
}