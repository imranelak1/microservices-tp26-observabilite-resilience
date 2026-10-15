# TP26 â€” Livres, rÃ©silience et concurrence

 J'ai sÃ©parÃ© les prix et les prÃªts de livres. Trois instances partagent MySQL et son volume. Les healthchecks attendent la base avant de lancer les services. Le stock est dÃ©crÃ©mentÃ© sous verrou pessimiste ; l'appel HTTP aux prix a des dÃ©lais maximum, trois tentatives et un circuit breaker. Le fallback est appliquÃ© aprÃ¨s les tentatives.

 ```sh
 mvn clean verify
 docker compose up -d --build
 python loadtest.py ID_LIVRE 50
 ```

 Les livres sont sur `http://localhost:18026/api/books` (autres instances : `18126`, `18226`), pricing sur `18326`. Le profil `dev` utilise H2 ; `docker` utilise MySQL. Pour tester le fallback, arrÃªter `pricing-service`, crÃ©er un livre avec du stock puis emprunter. Les mÃ©triques sont sur `/actuator/metrics`.

 Les traces d'exÃ©cution sont dans `evidence/` et les captures dans `screenshots/`.

 ![ExÃ©cution](screenshots/results.png)

![Fallback et m?triques](screenshots/fallback.png)

![Persistance MySQL](screenshots/persistence.png)
