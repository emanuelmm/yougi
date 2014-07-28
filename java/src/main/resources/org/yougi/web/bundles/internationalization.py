import os

locales = []
bundles = {}

for fileName in os.listdir("."):
    if fileName.startswith("Resources") and fileName.endswith(".properties"):
        f = open(fileName, 'r')
        bundle = {}
        while True:
            line = f.readline()
            if line == '':
                break

            if not line.startswith("#") and not line.find("=") == -1:
                keyValue = line.split("=")
                #print keyValue[0] +"="+ keyValue[1]
                bundle[keyValue[0]] = keyValue[1]

        try:
            locale = fileName[fileName.index("_") + 1:fileName.index(".")]
            bundles[locale] = bundle
            locales.append(locale)
        except ValueError:
            defaultLocale = "default"
            bundles[defaultLocale] = bundle
            locales.append(defaultLocale)

while True:
    key = raw_input('Search Key: ')
    if key == 'exit':
        break

    for locale in locales:
        print bundles[locale][key]