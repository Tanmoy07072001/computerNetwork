import socket
import time
from threading import *
import random

data_list=["e","e","e","e"]

sem=Semaphore(1)

def getBitCode(val):
    if val=="1":
        return 1
    elif val=="0":
        return -1
    else:
        return 0
    
#considering ranging between -4 to 4
def getDecimal(val):
    num=val[1:]
    deci=int(num,2)
    if(val[0]=="1"):
        deci=-deci
    return deci

#considering ranging between -4 to 4
def getBinary(num):
    sign="0"
    val=""
    if(num<0):
        sign="1"
        num=abs(num)
    for i in range(0,3):
        if(num & (1<<i)):
            val="1"+val
        else:
            val="0"+val
    val=sign+val
    return val

class Sender(Thread):
    def __init__(self,station_no,file_name) :
        super().__init__()
        self.file_name=file_name
        self.station_no=station_no
        f=open(self.file_name)
        self.content=f.read()
        self.content+="$"
        self.length=len(self.content)
        self.pos=0

    def run(self):
        while self.pos<self.length:
            time.sleep(random.uniform(0,1.5))
            if(data_list[self.station_no]!="e"):
                continue
            sem.acquire()
            data_list[self.station_no]=self.content[self.pos]
            sem.release()
            self.pos+=1
        

Walsh_table=[[1,1,1,1],[1,-1,1,-1],[1,1,-1,-1],[1,-1,-1,1]]

client_socket = socket.socket()
client_socket.connect(('localhost', 8000))

print("Connected to the server.........\n\n")

s0=Sender(0,"message0.txt")
s1=Sender(1,"message1.txt")
s2=Sender(2,"message2.txt")
s3=Sender(3,"message3.txt")

s0.start()
s1.start()
s2.start()
s3.start()

start=time.time()

while True:
    arr=[0,0,0,0]
    count=0
    sem.acquire()
    print("The data send to the server: ",data_list,"\n")
    for i in range(4):
        d=getBitCode(data_list[i])
        if data_list[i]=="$":
            count+=1
        else:
            data_list[i]="e"
        for j in range(4):
            temp=d*Walsh_table[i][j]
            arr[j]+=temp
    sem.release()
    if count==4:
        #send the end of transmission signal
        client_socket.send("$".encode())
        break
    data_word=""
    for i in range(4):
        data_word+=getBinary(arr[i])
    #send the data word to the server
    client_socket.send(data_word.encode())
    #receive the ack
    received_message = client_socket.recv(1024).decode()
    # if received_message=="ACk"
    #wait time slot
    time.sleep(0.5)

#transmission complete
print("Transmission complete")

end=time.time()

duration=end-start
print(f"Total time taken {duration}")