"""Charge réellement les trois instances et conserve chaque réponse HTTP."""
 import argparse, concurrent.futures, json, urllib.request, urllib.error, collections
 parser=argparse.ArgumentParser()
 parser.add_argument("book_id",type=int);parser.add_argument("requests",type=int,nargs="?",default=50)
 parser.add_argument("--ports",default="18026,18126,18226")
 args=parser.parse_args();ports=[int(x) for x in args.ports.split(",")]
 def borrow(i):
     port=ports[i%len(ports)];url=f"http://localhost:{port}/api/books/{args.book_id}/borrow"
     try:
         with urllib.request.urlopen(urllib.request.Request(url,method="POST"),timeout=30) as response: return {"port":port,"status":response.status,"body":response.read().decode()}
     except urllib.error.HTTPError as error:return {"port":port,"status":error.code,"body":error.read().decode()}
     except Exception as error:return {"port":port,"status":0,"body":str(error)}
 with concurrent.futures.ThreadPoolExecutor(max_workers=min(args.requests,50)) as pool:rows=list(pool.map(borrow,range(args.requests)))
 counts=collections.Counter(x['status'] for x in rows)
 print(json.dumps({"book_id":args.book_id,"requests":args.requests,"success":counts[200],"conflict":counts[409],"other":args.requests-counts[200]-counts[409],"responses":rows},ensure_ascii=False,indent=2))
 if args.requests-counts[200]-counts[409]:raise SystemExit(1)
