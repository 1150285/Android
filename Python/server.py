#!/usr/bin/env python

import socket
import RPi.GPIO as GPIO

TCP_IP = '192.168.11.28'
TCP_PORT = 5005
BUFFER_SIZE = 16  # Normally 1024

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((TCP_IP, TCP_PORT))
s.listen(1)

conn, addr = s.accept()
print 'Connection address:', addr

while (1):
    data = conn.recv(BUFFER_SIZE)
    print "\n Comando recebido >>> " + data + "\n"	
    if data == 'TESTCOMMUNICATION':
        print "Request from Andorid to Test Communication"
        #Send ACK
        #conn.send("OKCOMMUNICATION")  # echo
    elif data == 'STOPVIDEO':
        print "Request from Android"
    elif data == 'STARTVIDEO':
        print "Request from Android"       
    elif data == 'MOVEFRONT':
        print "Request from Android"  
    elif data == 'BREAK':
        print "Request from Android"   
    elif data == 'MOVEREAR':
        print "Request from Android"         
    elif data == 'MOVESTRAIGHT':
        print "Request from Android"  
    elif data == 'MOVELEFT15':
        print "Request from Android"   
    elif data == 'MOVELEFT30':
        print "Request from Android"  
    elif data == 'MOVELEFT45':
        print "Request from Android"    
    elif data == 'MOVERIGHT15':
        print "Request from Android"  
    elif data == 'MOVERIGHT30':
        print "Request from Android"  
    elif data == 'MOVERIGHT45':
        print "Request from Android"
    #else:
       # print "COMANDO NAO DEFINIDO!"  
    
   # if not data: break
   # print "received data:", data
   # conn.send(data)  # echo


conn.close()
