import socket
import time
import threading
import sys
import numpy as np

HOST = ''   # Symbolic name meaning all available interfaces
PORT = 2000 # Arbitrary non-privileged port

if __name__ == '__main__':
  # Datagram (udp) socket
  try :
      s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
      s.settimeout(1.0);
      print 'Socket created'
  except socket.error, msg :
      print 'Failed to create socket. Error Code : ' + str(msg[0]) + ' Message ' + msg[1]
      sys.exit()
   
   
  # Bind socket to local host and port
  try:
      s.bind((HOST, PORT))
  except socket.error , msg:
      print 'Bind failed. Error Code : ' + str(msg[0]) + ' Message ' + msg[1]
      sys.exit()
       
  print 'Socket bind complete'
  
  disp=0
  led=0
  cnt=0
   
  #now keep talking with the client
  while 1:
      data = bytearray([0xAA, 0x0C, 0x11, (cnt&0xFF), (disp&0xFF), ((disp>>8)&0xFF), (led&0xFF), 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00])
      remote_address = ('192.168.1.102', 2000)
      s.sendto(data , remote_address)
          
      # receive data from client (data, addr)
      try:
        d = s.recvfrom(1024)
        data = d[0]
        addr = d[1]
        
        print data

      except Exception as e:
          print(e)
          #raise(e)  # just for debugging
      except KeyboardInterrupt:
          print(e)
          exit(1)
          
      if cnt%5==0:
          led=led+1
          disp=disp+1
          
      print led&0xFF
          
      cnt=cnt+1
      time.sleep (50.0 / 1000.0);
   
  s.close()
      
  exit(0)
