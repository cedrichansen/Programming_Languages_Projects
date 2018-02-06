
#include <stdio.h>
#include <dirent.h>
#include <stdlib.h>
#include <string.h>

//Cedric Hansen
//Csc344hw01 09/19/2017

//node struct
struct node {
    struct node *characters [95];
    //if it's a "leaf" lead node, set to 1
    int end; 
};


//creates new trie nodes-this is a helper function
struct node * createNode() {
// allocate size for new node
    struct node *n1 =(struct node*) malloc(sizeof(struct node));    
   //mark new node as not being the end of a word
    n1->end = 0;        

    for (int i = 0;i<95; i++) {
    //create the "array" in each node, each slot representing a specific character
        n1->characters[i] = NULL;       
    }
    return n1;
}

//add "strings' into the trie
void insert(struct node *head, char *word) {
//create a random node to traverse through the trie
    struct node *current = head;

    //while there are more characters to insert, if the current character doesnt exist, create a new path
    for (int i = 0; i<strlen(word); i++) {
    // convert char to ascii value representation
        int q = word[i] - 32;   
        
        //if the current ascii value isnt found
        if (current->characters[q] == NULL) {    
        	// create a new node   
            current->characters[q] = createNode();  
        }
        //move to next node
        current = current->characters[q]; 
    }
    //just added a word so mark last char as being the end. ie, end =1
    current->end=1; 
}

struct node* contains(struct node *head, char *word) {

	// create random note to search through trie
    struct node *current = head;        
    for (int i=0; i<strlen(word); i++) {
        int q = word[i] - 32;
        if (current->characters[q]==NULL) { 
        //if there is no "next node" at a specific ascii index, return NULL (aka not found)
            return NULL;
        }
        //advance pointer through chars
        current = current->characters[q];   
    }
    return current;
} 


void print (struct node *n1, char *word) {
// create empty array for first part of word
    char first[100];        
    if (n1!=NULL) {  
    //as long as I pass in a node           
        for (int i=0; i<95; i++) { 
        //look through the "alphabet" of node 
        //copy word into the "prefix" array    
            strcpy(first, word);        
            //+32 to bring things back to their proper ascii values
            char toAdd[2] = {(char)(i+32) , '\0'};     
            //concat the prefix with the characters to add 
            strcat(first, toAdd);           
            //recursively call the print function
            print(n1->characters[i], first);        
        }
        //check if the end tag is 1 aka, if its a valid word
        if (n1->end == 1) {         
        // print out the word    
            printf("\n%s", word);       
        }
    }
}

//remove tabs from input
char* tabchecker(char *in) {        
    char *ret = strtok(in,"\t");
    return ret;
}



int main() {

    //get user input
    char *dName;
    printf("Enter a folder name: ");
    gets(dName);
    //start directory code
    DIR *directory;
    directory = opendir(dName);
    struct node *root = createNode();

    if (!directory) {
        printf("Invalid directory");
        return 1;
    }
        //add all of the file names into the trie
    while (100) {
        struct dirent *current;
        current = readdir(directory);
        if (!current) {
            break;
        }
        insert(root, current->d_name);
    }

    if (closedir(directory)) {
        printf("could not close directory");
        return 1;
    }
    //end directory code

    printf("> ");
    char input[100]; 
    gets(input);
    //delete the tab
    char *newin= tabchecker(input); 

    do {
        printf("\nfiles starting with %s in %s: \n", newin, dName);
        //node to look around and search
        struct node* n1 = contains(root, newin); 
        //self created function called print   
        print(n1, newin); 
        printf("\n\n%s","> ");
         //get new input
        gets(input);   
        newin = tabchecker(input);
    } while (strcmp(input, "exit") != 0);
    printf("Done! \n");

    return 0;
}

