#!/bin/bash

echo "Running Knowledge Sharing Platform Tests..."

echo ""
echo "Running Auth Service Tests..."
cd auth-service
mvn test
if [ $? -ne 0 ]; then
    echo "Auth Service tests failed!"
    exit 1
fi

echo ""
echo "Running Document Service Tests..."
cd ../document-service
mvn test
if [ $? -ne 0 ]; then
    echo "Document Service tests failed!"
    exit 1
fi

echo ""
echo "All tests completed successfully!"
