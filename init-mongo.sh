#!/bin/bash
set -e

DB_NAME="piadb"
DUMP_PATH="/docker-entrypoint-initdb.d/dump/$DB_NAME"

echo "🏁 Mongo init script starting..."
sleep 5

# Zkontroluj, jestli databáze už existuje
EXISTS=$(mongosh --quiet --eval "db.getMongo().getDBNames().includes('$DB_NAME')" | grep -c "true" || true)

if [ "$EXISTS" -eq 1 ]; then
  echo "✅ Database '$DB_NAME' already exists, skipping restore."
  exit 0
fi

echo "📦 Restoring dump for '$DB_NAME' from $DUMP_PATH ..."

mongorestore \
  --username admin \
  --password admin \
  --authenticationDatabase admin \
  --db "$DB_NAME" \
  --drop "$DUMP_PATH"

echo "✅ Dump restored successfully!"
