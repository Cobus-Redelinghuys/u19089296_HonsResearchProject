#include <iostream>
#include <fstream>
#include <string>

using namespace std;

int readConstant(){
    fstream newfile;
    newfile.open("./modules/Module7/config.txt");
    int res = 0;
    if(newfile.is_open()){
        string tp;
        getline(newfile, tp);
        res = stoi(tp);
        newfile.close();
    }
    return res;
}

void func(int n, int v){
    if(n < v){
        func(n-1, v);
    }
}

int main(int argc, char *argv[]){
    int n = readConstant();
    int v = stoi(argv[1]);
    func(n,v);
}