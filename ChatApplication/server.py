import socket

# Create a TCP/IP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Define host and port
host = 'localhost'
port = 8080

# Bind the socket to the port
sock.bind((host, port))

# Listen for incoming connections
sock.listen(1)
print('Waiting for a connection...')

# Accept a connection
connection, client_address = sock.accept()
print('Connection from:', client_address)

# Receive data in small chunks and send it back
data = connection.recv(16)
print('Received from client:', data.decode())

if data:
    # Echo the received data back to the client
    connection.sendall(data)
else:
    print('No data from', client_address)

# Close the connection
connection.close()
print('Connection closed')
