#!/usr/bin/env bash
 set -euo pipefail
 BOOK_ID="${1:-1}"; REQUESTS="${2:-50}"
 API_HOST="${LAB_HOST:-localhost}"
PORTS=(18026 18126 18226)
 work="$(mktemp -d)"
 for ((i=0;i<REQUESTS;i++)); do
   port="${PORTS[$((i%3))]}"
   curl -sS --max-time 30 -X POST "http://$API_HOST:$port/api/books/$BOOK_ID/borrow" -o "$work/$i.json" -w '%{http_code}' > "$work/$i.status" &
 done
 wait
 success=0; conflict=0; other=0
 for file in "$work"/*.status; do
   case "$(cat "$file")" in 200) success=$((success+1));; 409) conflict=$((conflict+1));; *) other=$((other+1));; esac
 done
 printf 'Success (200): %s\nConflict (409): %s\nOther: %s\nDetails: %s\n' "$success" "$conflict" "$other" "$work"
 test "$other" -eq 0
