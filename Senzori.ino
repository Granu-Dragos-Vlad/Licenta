const int xpin=A1;
const int ypin=A2;
const int zpin=A3;
const int pulse=A0;
unsigned int x,y,z,puls,valpuls;
unsigned int xsec,ysec,zsec,valpulssec;
bool read=true;
bool activ=false;
void setup() {
  Serial.begin(9600);

}
void loop() 
{
  puls=analogRead(pulse);
  valpuls=map(puls,0,1023,60,100);
  x=analogRead(xpin);
  y=analogRead(ypin);
  z=analogRead(zpin);
  if(read)
  {
    xsec=x;
    ysec=y;
    zsec=z;
    valpulssec=valpuls;
    read=false;
  }
  else
  {
    int diffx=xsec-x;
    int diffy=ysec-y;
    int diffz=zsec-z;
    if(diffx>=0 || diffy>=0 || diffz>=0)
    {
      activ=true;
    }
    int medievalpuls=(valpulssec+valpuls)/2;
    String activString =activ ? "Da" : "Nu";
    String date=String(medievalpuls)+" "+ activString;
    Serial.println(date);
  }
  delay(2000);
}
