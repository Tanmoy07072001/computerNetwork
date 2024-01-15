import socket

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

server_socket = socket.socket()
print("Socket Created")

server_socket.bind(("localhost", 8000))
server_socket.listen(1)

print("Waiting for connections....")

Walsh_table=[[1,1,1,1],[1,-1,1,-1],[1,1,-1,-1],[1,-1,-1,1]]
data_list=["","","",""]

client_socket, client_address = server_socket.accept()
print(f"\n\nConnection established with {client_address}")

while True:
    received_data = client_socket.recv(1024).decode()
    if(received_data=="$"):
        break
    arr=[]
    for i in range (0,len(received_data),4):
        temp=received_data[i:i+4]
        arr.append(getDecimal(temp))
    for i in range(4):
        sum=0
        for j in range(4):
            sum+=Walsh_table[i][j]*arr[j]
        sum/=4
        if(sum==0):
            print(f"Station {i} send nothing")
        elif sum==1:
            print(f"Station {i} send 1")
            data_list[i]+="1"
        else:
            print(f"Station {i} send 0")
            data_list[i]+="0"
    client_socket.send("ACK".encode())
    print("\n")


client_socket.close()

print("Transmission complete")

for i in range(4):
    print(f"Data received from station {i} :{data_list[i]}")
    fileName=f"receive{i}.txt"
    open(fileName,'w').write(data_list[i])

     