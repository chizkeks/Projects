// pacman.cpp: ���������� ����� ����� ��� ����������� ����������.
//
#include "stdafx.h"
#include <iostream>
#include <time.h> 
#include <stdio.h> 
#include <windows.h> 
#include <conio.h>

using namespace std;
const int mapI=15;
const int mapJ=17;

class karta
{friend class pacman;
friend class ghost;
friend int main();
private:
int map[mapI][mapJ];

karta() // ����������� �����
{

int map[mapI][mapJ]={
1,2,2,2,2,2,2,2,10,2,2,2,2,2,2,2,4,
3,13,0,0,0,0,0,0,3,0,0,0,0,0,0,0,3,
3,0,2,2,4,0,3,0,3,0,3,0,1,2,2,0,3,
3,0,0,0,3,0,3,0,3,0,3,0,3,0,0,0,3,
7,2,4,0,0,0,0,0,0,0,0,0,0,0,1,2,11,
3,12,3,0,1,2,2,0,0,0,2,2,4,0,3,12,3,
7,2,6,0,3,12,12,12,12,12,12,12,3,0,5,2,11,
3,0,0,0,3,12,3,12,12,12,3,12,3,0,0,0,3,
7,2,4,0,3,0,3,12,12,12,3,0,3,0,1,2,11,
3,12,3,0,3,0,5,2,2,2,6,0,3,0,3,12,3,
7,2,6,0,0,0,0,0,0,0,0,0,0,0,5,2,11,
3,0,0,0,3,0,3,0,3,0,3,0,3,0,0,0,3,
3,0,2,2,6,0,3,0,3,0,3,0,5,2,2,0,3,
3,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,3,
5,2,2,2,2,2,2,2,9,2,2,2,2,2,2,2,6
};
for (int i=0;i<mapI;i++) {
for (int j=0;j<mapJ;j++) // ������� �������- �����
{
switch (map[i][j]) {
case 1: {this->map[i][j]=201; break;};
case 2: {this->map[i][j]= 205; break;};
case 3: {this->map[i][j]=186; break;};
case 4: {this->map[i][j]=187; break;};
case 5: {this->map[i][j]= 200; break;};
case 6: {this->map[i][j]= 188; break;};
case 7: {this->map[i][j]= 204; break;};
case 8: {this->map[i][j]= 185; break;};
case 9: {this->map[i][j]= 202; break;};
case 10: {this->map[i][j]= 203; break;};
case 11: {this->map[i][j]= 185; break;};
case 12: { this->map[i][j]=32; break;};
case 13: {this->map[i][j]= 1; break;};
case 0: {this->map[i][j]= 250; break;};
default: {this->map[i][j]=32;break;}; 
}
}
}
};

void pechatkarty() // ������ ����� 
{for (int i=0;i<mapI;i++) {
for (int j=0;j<mapJ;j++)
{ printf("%c", map[i][j]);}
printf("\n"); 
}
};
void privetstvie () // ��������� ��������
{
cout<<"^^^^^^^^^^^^##########^^^^^^^^^^^^ \n";
cout<<"^^^^^^^^^########^^########^^^^^^^^^ \n";
cout<<"^^^^^^#####^^^^^^^^^^^^^^#####^^^^^^ \n";
cout<<"^^^^####^^^^^^^^^^^^^^^^^^^^####^^^^ \n";
cout<<"^^^###^^^^####^^^^^^^^####^^^^###^^^ \n";
cout<<"^^###^^^^######^^^^^^######^^^^###^^ \n";
cout<<"^###^^^^^######^^^^^^######^^^^^###^ \n";
cout<<"^###^^^^^######^^^^^^######^^^^^###^ \n";
cout<<"###^^^^^^^####^^^^^^^^####^^^^^^^### \n";
cout<<"###^^^##^^^^^^^^^^^^^^^^^^^^##^^^### \n";
cout<<"###^^^##^^^^^^^^^^^^^^^^^^^^##^^^### \n";
cout<<"^###^^###^^^^^^^^^^^^^^^^^^###^^###^ \n";
cout<<"^###^^^####^^^^^^^^^^^^^^####^^^###^ \n";
cout<<"^^###^^^######^^^^^^^^######^^^###^^ \n";
cout<<"^^^###^^^^^##############^^^^^###^^^ \n";
cout<<"^^^^####^^^^^^########^^^^^^####^^^^ \n";
cout<<"^^^^^^#####^^^^^^^^^^^^^^#####^^^^^^ \n";
cout<<"^^^^^^^^^########^^########^^^^^^^^^ \n";
cout<<"^^^^^^^^^^^^^##########^^^^^^^^^^^^^ \n";
cout<<"\n\n Welcome to Pacman game! Good luck in collecting pills! \n\n\n\n";
system("pause");
system("mode 25, 20");
};

friend class ghost;
friend class pacman;
};

class ghost; // ��������������� ���������� ������, ��� ������������� ������� over

class pacman // ����� ������� 
{
friend class ghost;
friend class karta;
friend int main();
public:
int score;
private:
int pacmanI,pacmanJ;

pacman(karta a,int x, int y) // ����������� �������
{
	score=0;
	pacmanI=x;
	pacmanJ=y;
	a.map[pacmanI][pacmanJ]=1;}

karta getKeyPress(karta a) // ������� ���������� �������� �������
{
if(GetAsyncKeyState(VK_UP)) // �������� �����
{
if ( a.map[pacmanI- 1][pacmanJ] == 32 ) // ���� ��������� ������ �������
{
a.map[pacmanI][pacmanJ] = 32;
a.map[pacmanI - 1][pacmanJ] = 1;
pacmanI--;
}
if(a.map[pacmanI- 1][pacmanJ] == 250) // ���� ��������� ������ ������
{
a.map[pacmanI][pacmanJ] = 32;
a.map[pacmanI - 1][pacmanJ] = 1;
pacmanI--;
score++;
}
if (a.map[pacmanI-1][pacmanJ] == 5) // ���� ��������� ������ ���������� (��� ���������� ������� vkusno)
{
	a.map[pacmanI][pacmanJ] = 32;}
}
else if (GetAsyncKeyState(VK_LEFT)) // �������� ����� 
{
if ( a.map[pacmanI][pacmanJ- 1] ==32) // ���� ��������� ������ �������
{ 
a.map[pacmanI][pacmanJ] = 32;
a.map[pacmanI][pacmanJ - 1] = 1;
pacmanJ--;
}
if (a.map[pacmanI][pacmanJ- 1]==250)  // ���� ��������� ������ ������
{
a.map[pacmanI][pacmanJ] = 32;
a.map[pacmanI][pacmanJ - 1] = 1;
pacmanJ--;
score++;
}
if (a.map[pacmanI][pacmanJ-1] == 5)   // ���� ��������� ������ ���������� (��� ���������� ������� vkusno)
{
	a.map[pacmanI][pacmanJ] = 32;}
}

else if (GetAsyncKeyState(VK_RIGHT)) // �������� ������
{
if ( a.map[pacmanI][pacmanJ+ 1] == 32) // ���� ��������� ������ �������
{
a.map[pacmanI][pacmanJ] = 32;
a.map[pacmanI][pacmanJ + 1] = 1;
pacmanJ++;
}
if (a.map[pacmanI][pacmanJ+1]== 250)   // ���� ��������� ������ ������
{
a.map[pacmanI][pacmanJ] = 32;
a.map[pacmanI][pacmanJ + 1] = 1;
pacmanJ++;
score++;
}
if (a.map[pacmanI][pacmanJ+1] == 5)   // ���� ��������� ������ ���������� (��� ���������� ������� vkusno)
{
	a.map[pacmanI][pacmanJ] = 32;}
}
else if (GetAsyncKeyState(VK_DOWN)) // �������� ����
{
if ( a.map[pacmanI+1][pacmanJ] == 32)  // ���� ��������� ������ ������� 
{
a.map[pacmanI][pacmanJ] = 32;
a.map[pacmanI + 1][pacmanJ] = 1;
pacmanI++;
}
if (a.map[pacmanI+1][pacmanJ] == 250)  // ���� ��������� ������ ������
{
a.map[pacmanI][pacmanJ] = 32;
a.map[pacmanI + 1][pacmanJ] = 1;
pacmanI++;
score++;
}
if (a.map[pacmanI+1][pacmanJ] == 5)  // ���� ��������� ������ ���������� (��� ���������� ������� over)
{
	a.map[pacmanI][pacmanJ] = 32;

}
}
return(a);
};

void check(karta a)
{
if(score==101)
{ system ("cls");
cout<<"_________$$____________________$$_______\n";
cout<<"__$$_____$__$__________________$__$_____$$\n";
cout<<"_$__$____$__$__________________$__$____$__$ \n";
cout<<"__$__$___$__$__________________$__$___$__$ \n";
cout<<"___$__$__$__$__________________$__$__$__$ \n";
cout<<"____$__$_$__$__________________$__$_$__$\n";
cout<<"____$$__$$__$ _________________ $__$$__$$ \n";
cout<<"_$$__$_$$$$$_____Well_____ _$$$$$_$__$$\n";
cout<<"_$__$__$_____$____done!____$_____$__$__$ \n";
cout<<"$_$__$__$$$___$_All coins_$__$$$__$__$_$\n";
cout<<"_$_$__$__$_$__$_collected!_$__$_$__$__$_$\n";
cout<<"_$_$__$$_$___$______________$___$_$$__$_$\n";
cout<<"__$_$$______$________________$______$$_$ \n";
cout<<"___$_______$__________________$_______$ \n";
cout<<"___$______$___________________$______$  \n";
cout<<"__$$$$$$$$$$______________$$$$$$$$$$\n";
cout<<"$$$$$$$$$$$$$___________$$$$$$$$$$$$\n \n \n \n ";
system("pause");
exit(0);}
};

friend void over(pacman a, ghost b); // ���������� ������������� �������
};


class ghost { // ����� ����������
friend class pacman;
friend class karta;
friend int main();

private:
int ghostI;
int ghostJ;

ghost(karta b,int x, int y)  // ����������� ����������
{
	ghostI=x;
	ghostJ=y;
b.map[ghostI][ghostJ]=5;
};

karta Move(karta a)  // ���������� 
{
int k=(rand() % 4) + 1; // �������� � ��������� ������� : 1 - ����,2- ����� ,3- ����� ,4- ������
if(k==1) 
{ 
if (a.map[ghostI - 1][ghostJ] == 250)
{ 
a.map[ghostI - 1][ghostJ] = 5;
a.map[ghostI][ghostJ] = 250;
ghostI--;
}
if (a.map[ghostI - 1][ghostJ] == 32 || a.map[ghostI - 1][ghostJ] == 1)
{
a.map[ghostI][ghostJ] = 32;
a.map[ghostI - 1][ghostJ] = 5;
ghostI--;
}
return(a);
}
if (k==2)
{
if ( a.map[ghostI + 1][ghostJ] == 250)
{
a.map[ghostI + 1][ghostJ] = 5;
a.map[ghostI][ghostJ] = 250;
ghostI++;
}
if(a.map[ghostI + 1][ghostJ] ==32 || a.map[ghostI + 1][ghostJ] ==1)
{
a.map[ghostI][ghostJ] = 32;
a.map[ghostI + 1][ghostJ] = 5;
ghostI++;
}
return(a);
}

if (k==3)
{
if ( a.map[ghostI][ghostJ - 1] == 250)
{
a.map[ghostI][ghostJ - 1] = 5;
a.map[ghostI][ghostJ] = 250;
ghostJ--;
}
if (a.map[ghostI][ghostJ - 1] ==32 || a.map[ghostI][ghostJ - 1] ==1)
{
a.map[ghostI][ghostJ] = 32;
a.map[ghostI][ghostJ - 1] = 5;
ghostJ--;
}
return (a);
}

if(k==4) // �������� 
{
if ( a.map[ghostI][ghostJ + 1] == 250)
{
a.map[ghostI][ghostJ + 1] = 5;
a.map[ghostI][ghostJ] = 250;
ghostJ++;
}
if (a.map[ghostI][ghostJ + 1] ==32 || a.map[ghostI][ghostJ + 1] == 1)
{
a.map[ghostI][ghostJ] = 32;
a.map[ghostI][ghostJ + 1] = 5;
ghostJ++;
}
return (a);
}
};

friend void over(pacman a, ghost b); // ���������� ������������� �������
};

void over(pacman a, ghost b) // ������� "��������" ���������� �������
{
if(a.pacmanI==b.ghostI && a.pacmanJ==b.ghostJ)
{
system ("cls"); // ������� ������
system("mode 80,40");
cout<<"\n"<<"YOU LOST! Maybe next time you�ll win!"<<"\n\n\n";
system("pause");
exit(0);
}
};

int main()
{
karta k;
pacman z(k,1,1);
ghost g(k,4,9);
k.privetstvie();
k.pechatkarty();
for(;;)
{
Sleep(100);
system("cls");
k=z.getKeyPress(k);
k=g.Move(k);
k.pechatkarty();
z.check(k);
cout<<"Score: "<<z.score<<"\n";
over(z,g);
_getch();
_getch();
}
};



