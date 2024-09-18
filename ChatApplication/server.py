import socket
import threading

# Function to handle client connections
def handle_client(connection, client_address):
    print('Connection from:', client_address)
    try:
        while True:
            # Receive data in small chunks
            data = connection.recv(16)
            if data:
                print('Received from client:', data.decode())
                # Echo the received data back to the client
                connection.sendall(data)
            else:
                print('No data from', client_address)
                break
    finally:
        # Close the connection
        connection.close()
        print('Connection closed for:', client_address)

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

while True:
    # Accept a connection
    connection, client_address = sock.accept()
    # Create a new thread for the client connection
    client_thread = threading.Thread(target=handle_client, args=(connection, client_address))
    client_thread.start()
