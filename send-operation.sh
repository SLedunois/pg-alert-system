#!/bin/bash
curl -d "{\"amount\": $1}" -H "Content-Type: application/json" -X POST http://localhost:8080/accounts/1/operations