## Local Dev Setup — DynamoDB Local (Persistent) + SAM Local on `samnet`

---

### 1) Start DynamoDB Local (persistent)

```bash
docker rm -f dynamodb-local 2>/dev/null || true

docker run -d --name dynamodb-local \
  --network samnet \
  -p 18001:8000 \
  -v dynamodb_data:/home/dynamodblocal/data \
  amazon/dynamodb-local \
  -jar DynamoDBLocal.jar -sharedDb -dbPath /home/dynamodblocal/data
```

### 2) Create tables + seed counter (idempotent)

Creates:

- `global_counter` (partition key: `name` string)
  - Seeds item: `name=url_id_counter`, `value=0`
- `url_mapping` (partition key: `alias` string)

```bash
docker run --rm --network samnet \
  --entrypoint sh \
  -e AWS_ACCESS_KEY_ID=dummy \
  -e AWS_SECRET_ACCESS_KEY=dummy \
  -e AWS_REGION=us-east-1 \
  amazon/aws-cli -lc '
set -e

echo "Waiting for DynamoDB Local..."
until aws dynamodb list-tables --endpoint-url http://dynamodb-local:8000 --region us-east-1 >/dev/null 2>&1; do
  sleep 1
done
echo "DynamoDB Local is up."

echo "Creating global_counter (ignore if exists)..."
aws dynamodb create-table \
  --table-name global_counter \
  --attribute-definitions AttributeName=name,AttributeType=S \
  --key-schema AttributeName=name,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST \
  --endpoint-url http://dynamodb-local:8000 \
  --region us-east-1 || true

echo "Seeding url_id_counter (ignore if exists)..."
aws dynamodb put-item \
  --table-name global_counter \
  --item "{\"name\":{\"S\":\"url_id_counter\"},\"value\":{\"N\":\"0\"}}" \
  --endpoint-url http://dynamodb-local:8000 \
  --region us-east-1 || true

echo "Creating url_mapping (ignore if exists)..."
aws dynamodb create-table \
  --table-name url_mapping \
  --attribute-definitions AttributeName=alias,AttributeType=S \
  --key-schema AttributeName=alias,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST \
  --endpoint-url http://dynamodb-local:8000 \
  --region us-east-1 || true

echo "Tables:"
aws dynamodb list-tables --endpoint-url http://dynamodb-local:8000 --region us-east-1
'
```

### 3) Verify tables (from host)

If you have AWS CLI installed on your Mac:

```bash
export AWS_ACCESS_KEY_ID=dummy
export AWS_SECRET_ACCESS_KEY=dummy
export AWS_REGION=us-east-1
export AWS_EC2_METADATA_DISABLED=true

aws dynamodb list-tables \
  --endpoint-url http://localhost:18001 \
  --region us-east-1
```

### 4) Start SAM Local API on samnet

```bash
sam local start-api --docker-network samnet --debug
```

