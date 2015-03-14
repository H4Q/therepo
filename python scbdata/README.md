# Enkel python kodsnutt för att hämta data från SCB

*bidrag utav [Johan Frisk](mailto:johan.frisk@hiq.se)*

Hej.

Du får filen under BSD licens via mail istället.

Det finns flera klasser i filen, men det enda som du borde behöva är funktionen getTable

Så kod för att hämta för allmäna val borde vara:

import ScbData

response = ScbData.getTable(' http://api.scb.se/OV0104/v1/doris/sv/ssd/ME/ME0104/ME0104C/ME0104T3/')

Formatet blir ett python dict tror jag

Du måste installera alla dependencies med pip install

*För att fixa BOM-buggen i biblioteket requests* måste du ändra i funktionen json i models.py

ta bort kollen av encoding i den yttersta if-satsten. det skall se ut såhär:

    if  len(self.content) > 3:

Ring mig eller kom förbi om du stöter på problem