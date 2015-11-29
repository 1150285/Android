import SocketServer

class MyTCPHandler(SocketServer.BaseRequestHandler):
    """
    The RequestHandler class for our server.

    It is instantiated once per connection to the server, and must
    override the handle() method to implement communication to the
    client.
    """
    
    #Protocolo / Rever switch collection
    #TESTCOMUNICATION #TESTVIDEO #STARTVIDEO #STOPVIDEO #MOVEFRONT #MOVEREAR 
    #MOVELEFT20 #MOVELEFT40 #MOVELEFT60 #MOVERIGHT20 #MOVERIGHT40 #MOVERIGHT60
 
    def handle(self):
        # self.request is the TCP socket connected to the client
        self.data = self.request.recv(1024).strip()
        print "{} wrote:".format(self.client_address[0])
        print self.data

	self.tokens = self.data.split(' ', 1)
        self.command = self.tokens[0]
	print "Comando recebido >>> " + self.command	
	if self.command == 'TESTCOMMUNICATION':
		print "Request from Andorid to Test Communication"
	elif self.command == 'TESTVIDEO':
		print "Request from Andorid to Test Video"    
	elif self.command == 'MOVEFRONT':
		print "Request from Andorid to Move Front"
	elif self.command == 'MOVEREAR':
		print "Request from Andorid to Move Rear" 
	elif self.command == 'STARTVIDEO':
		print "Request from Andorid to Strat Video" 
	elif self.command == 'STOPVIDEO':
		print "Request from Andorid to Stop Video" 
	else:
         print "COMANDO NAO DEFINIDO!"  

if __name__ == "__main__":
    HOST, PORT = "localhost", 9999

    # Create the server, binding to localhost on port 9999
    server = SocketServer.TCPServer((HOST, PORT), MyTCPHandler)

    # Activate the server; this will keep running until you
    # interrupt the program with Ctrl-C
    server.serve_forever()
