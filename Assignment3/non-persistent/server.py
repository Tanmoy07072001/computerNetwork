import socket
from injectError import *
import time
from threading import *
import random

server_socket = socket.socket()
print("Socket Created")

server_socket.bind(("localhost", 8000))
server_socket.listen(1)

print("Waiting for connections....")

client_socket, client_address = server_socket.accept()
print(f"\n\nConnection established with {client_address}")
client_socket.settimeout(1)
#socket configuration ends here


#for synchronization
sem=Semaphore(1)
isBusy=False

#for performance measurement
time_list=[0,0,0,0,0,0,0]#7
total_bit=0


class Sender(Thread):
    def __init__(self, file_name,station_no):
        super().__init__()
        self.file_name = file_name
        self.header = '1011000101001010'  # it is set as a constant
        self.station_no=station_no
        self.frame_size = 32  # Set the frame size to 32
        self.file_content = self.read_file()
        if self.file_content is not None:
            # print("File Contents:")
            # print(self.file_content)
            # print("Length of the file content:", len(self.file_content), "\n")
            self.payloads = self.break_into_payloads()
            # print("Payloads :")
            # print(self.payloads,"\n")
            self.frames = self.break_into_frames(self.payloads)
            # print(self.frames)
            # print(f"Frames (total frame= {len(self.frames)}) : ")
            # for frame in self.frames : 
            #     print(frame)
            # print("\n")

    def read_file(self):
        try:
            # with open(self.file_name, 'r') as file:
            #     content = file.read()
            #     return content
            f=open(self.file_name)
            return f.read()
        except FileNotFoundError:
            print(f"Error: File '{self.file_name}' not found.")
            return None
        except Exception as e:
            print(f"Error: Unable to read file '{self.file_name}'.")
            print(f"Exception: {e}")
            return None
        
    def run_injectError(self, payload):
        error_injector = InjectError(payload)
        return error_injector.inject_error()


    def break_into_payloads(self):
        if self.file_content is None:
            return None

        payloads = []
        for i in range(0, len(self.file_content), self.frame_size):
            payloads.append(self.file_content[i:i + self.frame_size])
        return payloads
    

    def divide_into_substrings(self, string):
        substrings = []
        for i in range(0, len(string), 16):
            substrings.append(string[i:i + 16])
        return substrings

    def calculate_checksum(self, substrings):
        total_sum = 0
        for substring in substrings:
            binary_num = int(substring, 2)
            total_sum += binary_num
            carry = total_sum >> 16
            total_sum = (total_sum & 0xFFFF) + carry

        checksum = total_sum & 0xFFFF
        checksum = ~checksum & 0xFFFF

        checksum_str = ""
        for i in range(16):
            val = (checksum & 1)
            checksum_str = str(val) + checksum_str
            checksum >>= 1

        return checksum_str

    def break_into_frames(self, payloads):
        frames = []
        frame_seq=0
        for payload in payloads:
            trailer = self.calculate_checksum(self.divide_into_substrings(payload))
            frame = self.header +':'+str(self.station_no) +':' + str(frame_seq) + ":" + payload + ':' + trailer
            # calculate crc or checksum and append it as well
            frames.append(frame)
            frame_seq+=1

        # print("\nFrames: ")
        # print(frames)

        return frames
    
    #code run on seperate thread and write the logic for persistent method
    def run(self):
        global isBusy
        global total_bit
        frames_len = len(self.frames)
        curr_frame = 0


        start=time.time()

        while curr_frame < frames_len:
            message_to_send = self.frames[curr_frame]
            parts = message_to_send.split(':')
            header,station_no, seq, payload, received_checksum = parts
            errored_payload = self.run_injectError(payload)
            new_frame = self.header + ':'+station_no+':' + seq + ":" + errored_payload + ':' + received_checksum

            message_bytes = bytes(new_frame, "utf-8")

            #sensing of channel for transmission
            can_use = False
            while True:
                sem.acquire()
                if(isBusy==False):
                    isBusy=True
                    can_use=True
                sem.release()
                if can_use==True:
                    break
                else:
                    #wait for a random amount of time
                    rand_time=random.randint(0,100)/100
                    print(f"Station {self.station_no} waiting for {rand_time}s")
                    time.sleep(rand_time)

            client_socket.send(message_bytes)
            total_bit+=len(message_bytes)
            print(f"Frame {curr_frame} sent of station {self.station_no}")#add station number

            try:
                received_data = client_socket.recv(1024).decode()
                # print(f"Received from Client: {received_data}")

                if received_data == "ACK":
                    print(f"Received ACK from the client for frame {curr_frame} of station {self.station_no}")#add station number
                    curr_frame += 1
            except socket.timeout:
                print(f"Timeout: No ACK received. Resending frame {curr_frame} of station {self.station_no}.")#add station number
                continue 
            finally:
                sem.acquire()
                isBusy=False
                sem.release()

        end=time.time()
        time_list[self.station_no-1]=end-start







file_name="message_data.txt"
sender1 = Sender(file_name,1)
sender2 = Sender(file_name,2)
sender3 = Sender(file_name,3)

start=time.time()

sender1.start()
sender2.start()
sender3.start()
sender1.join()
sender2.join()
sender3.join()

end=time.time()

client_socket.close()

duration=end-start
print("\n\n--------------------------------------------------")
for i in range(3):
    print(f"Time taken to complete the transmission for station {i+1} is {time_list[i]} s")

print("\n\n--------------------------------------------------")
print(f"Total time taken to complete the transmission {duration} s")
print(f"total bits send {total_bit}")
throughtput=total_bit/duration
print(f"Throughput={throughtput}bits/s")
