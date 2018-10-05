# Chat-Room
This program is a Java application that allows the user to setup a chat room server and have multiple clients connect to the chat room server. The chat room is encrypted using AES, and keys are sent to each other using ECDS. The chat server can create logfiles so that the server administrators can later view what was going on.

## Images
Example chatroom (both server and client perspectives):
![Sample chatroom](https://preview.ibb.co/n4wo9K/chatroom.png "Sample chatroom")

Example logfile (this is from a different chatroom than the above one):
![Sample logfile](https://image.ibb.co/hPKQpK/logfile.png "Sample logfile")

## Usage
The code for the server is under the /Chat Server/ directory, and the code for the client is under the /Chat Client/ directory. Since these two programs were made using two different Eclipse projects, you should easily be able to import this into your Eclipse workspace.
