#!/bin/bash
if [ -d "root" ]; then
  echo "Setting up dummy user: username and password are guest."
  echo "This does not currently initialize guest in the server password files..."

  java jammed.UserData

  if [ ! -d "root/users/guest" ]; then
    mkdir "root/users/guest"
  fi

  mv "guest_IV.txt" "root/users/guest"
  mv "guest_USERDATA.txt" "root/users/guest"

  if [ -f "root/users/guest/guest_LOG.txt" ]; then
    rm "root/users/guest/guest_LOG.txt"
  fi

  touch "root/users/guest/guest_LOG.txt"
else
  echo "Cannot initialize: no existing database."
fi
