# SSCFSE BP -- Saint Seiya Cosmo Fantasy - Battles Populator

### In a raining day, I discovered a smartphone rpg game "Saint Seiya Cosmo Fantasy" - after a many many battles, I collected a lot of win/lose battles screenshot, and I decided to build a semi-automatic database.

0. from smartphone, screenshots are automatically backupped on GooglePhotos album
1. each night, this tool retrieves those files from that album: if any photos has already been processed, just skip
2. use Google Cloud Vision to extract text info from those new images (battle info, enemy, and so on)
3. a new record containing image url, extracted info, other some knowledge will be added in a local db and putted as row onto a google spreadsheet
4. that spreadsheet acts as data provider for a nth tab ("battles") on sscfse portal: http://www.sscfse.iubris.net

Just java and a nip of bash ;)
