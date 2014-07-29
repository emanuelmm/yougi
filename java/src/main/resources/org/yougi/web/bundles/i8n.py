import os

locales = []
bundles = {}

print "Leave the key empty to exit.\n"
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
                bundle[keyValue[0]] = keyValue[1]

        f.close()

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
    if key == '':
        break

    change = 'n'
    try:
        for locale in locales:
            print '{0:7}: {1:8}'.format(locale, bundles[locale][key].rstrip('\n'))
        change = raw_input('Change ? (y, n): ')
    except KeyError:
        create = raw_input("The key '{0}' does not exist. Do you want to create it? (y, n): ".format(key))
        if create == 'y':
            for locale in locales:
                bundles[locale][key] = raw_input("{0}: ".format(locale))

    if change == 'y':
        for locale in locales:
            newValue = raw_input("%s: from '%s' to " % (locale, bundles[locale][key].rstrip('\n')))
            if newValue == '':
                newValue = bundles[locale][key]
            bundles[locale][key] = newValue