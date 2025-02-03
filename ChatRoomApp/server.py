import threading
import socket
import signal
import sys

# Local host
host = '127.0.0.1'
port = 55000

server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server.bind((host, port))
server.listen()

# Timeout to make server.accept() non-blocking
server.settimeout(1.0)

clients = []
usernames = []
lock = threading.Lock()
running = True

def broadcast(message):
    with lock:
        for client in clients:
            try:
                # Check if client is still connected before sending the message
                if client.fileno() != -1:
                    client.send(message)
            except (socket.error, ConnectionResetError) as e:
                print(f"Failed to send message to client: {e}")
                with lock:
                    if client in clients:
                        clients.remove(client)
                        client.close()

def handle(client):
    try:
        while True:
            message = client.recv(1024)
            if not message:
                break
            broadcast(message)
    except (socket.error, ConnectionResetError) as e:
        print(f"Error while handling message: {e}")
    finally:
        # Safely handle username removal before removing the client
        username = None
        with lock:
            if client in clients:
                index = clients.index(client)
                username = usernames[index]
                clients.remove(client)
                usernames.remove(username)
                client.close()

        if username:
            broadcast(f'{username} has left the chat.'.encode('ascii'))

def receive():
    print("Server is running...")
    while running:
        try:
            client, address = server.accept()
            print(f'Connected with {str(address)}')

            client.send('USERNAME'.encode('ascii'))
            username = client.recv(1024).decode('ascii')
            with lock:
                usernames.append(username)
                clients.append(client)

            print(f'Username of the client is {username}')
            broadcast(f'{username} has joined the chat.\n'.encode('ascii'))
            client.send('Connected to the server.'.encode('ascii'))

            thread = threading.Thread(target=handle, args=(client,), daemon=True)
            thread.start()
        except socket.timeout:
            continue
        except Exception as e:
            print(f"Error accepting client connection: {e}")

def shutdown_server(signal, frame):
    global running
    print("\nShutting down the server...")
    running = False
    # Close all client connections
    with lock:
        for client in clients:
            client.close()
    server.close()

    # Wait for all client handling threads to finish
    for thread in threading.enumerate():
        if thread is not threading.main_thread():
            thread.join()

    sys.exit(0)

# Set the SIGINT signal handler to properly shutdown server
signal.signal(signal.SIGINT, shutdown_server)

# Start the server
try:
    receive()
except Exception as e:
    print(f"Server encountered an error: {e}")
    shutdown_server(None, None)
