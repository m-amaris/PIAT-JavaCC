# include <iostream>

 using namespace std;
 
 int main(){
         int N;
         int suma = 0;
         cout << "Introduzca la cantidad de números a leer: \n";
         int numero;
         cin >> N;
         for(int i = 0; i < N; i++){
                 cout << "Introduzca el número " << i+1 << ": ";
                 cin >> numero;
                 suma = suma + numero;
         }
         cout << "La suma de los " << N << " numeros es: " << suma << "\n";
         return 0;
 }
