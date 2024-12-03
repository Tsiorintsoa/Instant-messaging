@echo off

cd "../out/production/instant-messaging"

:: launch server
start cmd.exe /K "java scripts/ServerScript"

:: connect two clients
start cmd.exe /K "java scripts/ClientScript"
start cmd.exe /K "java scripts/ClientScript"
