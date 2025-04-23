import xml.etree.ElementTree as ET

report_path = 'target/site/jacoco/jacoco.xml'
output_path = 'righe_non_coperte.txt'

tree = ET.parse(report_path)
root = tree.getroot()

with open(output_path, 'w') as out:
    for package in root.findall('package'):
        package_name = package.get('name').replace('.', '/')
        for sourcefile in package.findall('sourcefile'):
            filename = sourcefile.get('name')
            for line in sourcefile.findall('line'):
                if line.get('mi') != '0':
                    line_number = line.get('nr')
                    out.write(f"src/main/java/{package_name}/{filename}:{line_number}\n")

print(f"Creato file: {output_path}")
