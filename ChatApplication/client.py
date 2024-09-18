import socket

# Create a TCP/IP socket
stream_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Define host and port
host = 'localhost'
port = 8080

# Connect the socket to the server
server_address = (host, port)
print("Connecting to server...")
stream_socket.connect(server_address)

# Send data
message = 'Hello, Server!'
stream_socket.sendall(message.encode())

# Receive response from the server
data = stream_socket.recv(16)
print('Received from server:', data.decode())

# Close the socket
stream_socket.close()
print('Socket closed')
