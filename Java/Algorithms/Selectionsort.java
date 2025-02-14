//A method for sorting an array of strings lexicographically
public static String[] selectionsortstring (String[] strings){
    for(int i = 0; i < strings.length-1; i++){
        int swap_index = i;
        for(int j = i+1; j < strings.length; j++){
            if(strings[j].compareToIgnoreCase(strings[swap_index]) < 0){
                swap_index = j;        
            }   
        } 
        if(swap_index != i){
            String temp_String = strings[i];
            strings[i] = strings[swap_index];
            strings[swap_index] = temp_String;
        }
    }
    return strings;   
}