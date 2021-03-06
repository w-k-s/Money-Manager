#!/usr/bin/env bash

# https://devhints.io/bash

ENDPOINT=http://localhost:8080/ws

function TEST_RECORD_TRANSACTION(){
  echo 'TEST RECORD TRANSACTION ...';

  local RESULT=$(curl -s -X POST -H 'Content-Type: text/xml' -d @requests/recordTransaction.xml $ENDPOINT)
  echo "$RESULT" > responses/TEST_RECORD_TRANSACTION.xml;

  TEST_UUID=$(echo "$RESULT" | sed -rn 's/.*<ns2:uuid>(.*)<\/ns2:uuid>.*/\1/p')


  if [ ${#TEST_UUID} = 36 ]; then
    echo 'OK'
  else
    echo 'FAILURE'
  fi
}

function TEST_GET_RECORD_BY_UUID(){
  echo 'TEST_GET_RECORD_BY_UUID ...';

  # https://stackoverflow.com/a/415775/821110
  local CONTENT=$(sed -e "s/\${uuid}/$TEST_UUID/" requests/getTransactionsByUuid.xml)
  local RESULT=$(curl -s -X POST -H 'Content-Type: text/xml' -H 'SOAPAction:http://www.wks.io/moneymanager/gen/transaction/findByUuid' -d "$CONTENT" $ENDPOINT)
  echo "$RESULT" > responses/TEST_GET_RECORD_BY_UUID.xml;

  local RETURN_UUID=$(echo "$RESULT" | sed -rn 's/.*<ns2:uuid>(.*)<\/ns2:uuid>.*/\1/p')
  local RETURN_CREATED_BY=$(echo "$RESULT" | sed -rn 's/.*<ns2:createdBy>(.*)<\/ns2:createdBy>.*/\1/p')

  if [[ "$RETURN_UUID" = "$TEST_UUID" && "$RETURN_CREATED_BY" = "user" ]]; then
    echo 'OK'
  else
    echo 'FAILURE'
  fi
}

function TEST_GET_RECORDS_BETWEEN_DATES(){
  echo 'TEST_GET_RECORDS_BETWEEN_DATES ...';

  local FROM_DATE='2021-01-16'
  local TO_DATE='2021-01-17'
  # https://stackoverflow.com/a/415775/821110
  local CONTENT=$(sed -e "s/\${from}/$FROM_DATE/" -e "s/\${to}/$TO_DATE/" requests/getTransactionsRequest.xml)
  local RESULT=$(curl -s -X POST -H 'Content-Type: text/xml'  -d "$CONTENT" $ENDPOINT)
  echo "$RESULT" > responses/TEST_GET_RECORDS_BETWEEN_DATES.xml;

  if [[ "$RESULT" == *"$TEST_UUID"* && "$RESULT" == *"2021-01-16"* ]]; then
    echo 'OK'
  else
    echo 'FAILURE'
  fi
}

function TEST_GET_TOTAL_EXPENSES_PER_CATEGORY(){
  echo 'TEST_GET_TOTAL_EXPENSES_PER_CATEGORY ...';

  local YEAR='2021'
  local MONTH='01'
  # https://stackoverflow.com/a/415775/821110
  local CONTENT=$(sed -e "s/\${year}/$YEAR/" -e "s/\${month}/$MONTH/" requests/getTotalExpensesPerCategory.xml)
  local RESULT=$(curl -s -X POST -H 'Content-Type: text/xml'  -d "$CONTENT" $ENDPOINT)
  echo "$RESULT" > responses/TEST_GET_TOTAL_EXPENSES_PER_CATEGORY.xml;

  if [[ "$RESULT" == *"Car/Cleaning"* ]]; then
    echo 'OK'
  else
    echo 'FAILURE'
  fi
}

function TEST_ERROR_WHEN_RECORD_DOES_NOT_EXIST(){
  echo 'TEST_ERROR_WHEN_RECORD_DOES_NOT_EXIST ...';

  local CONTENT=$(sed -e "s/\${uuid}/a9d7b490-58fd-11eb-ae93-0242ac130002/" requests/getTransactionsByUuid.xml)
  local RESULT=$(curl -s -X POST -H 'Content-Type: text/xml' -H 'SOAPAction:http://www.wks.io/moneymanager/gen/transaction/findByUuid' -d "$CONTENT" $ENDPOINT)
  echo "$RESULT" > responses/TEST_ERROR_WHEN_RECORD_DOES_NOT_EXIST.xml;

  if [[ "$RESULT" == *"404_TRANSACTION_NOT_FOUND"* ]]; then
    echo 'OK'
  else
    echo 'FAILURE'
  fi
}

function TEST_WSDL(){
  echo 'TEST_WSDL ...';

  local RESULT=$(curl -s -X GET "$ENDPOINT/transactions.wsdl")
  echo "$RESULT" > responses/TEST_WSDL.xml;

  if [[ -n "$RESULT" ]]; then
    echo 'OK'
  else
    echo 'FAILURE'
  fi
}

TEST_RECORD_TRANSACTION;
TEST_GET_RECORD_BY_UUID;
TEST_GET_RECORDS_BETWEEN_DATES;
TEST_GET_TOTAL_EXPENSES_PER_CATEGORY;
TEST_ERROR_WHEN_RECORD_DOES_NOT_EXIST;
TEST_WSDL;