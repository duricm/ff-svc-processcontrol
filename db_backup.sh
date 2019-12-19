/opt/PostgreSQL/10/bin/pg_dump -U postgres -w -F t "postgresql://3.136.241.73:5432/bitcoin-card?user=postgres&password=$1" > /opt/PostgreSQL/10/data/bitcoin-card.tar
