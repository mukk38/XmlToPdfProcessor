# SpringFopConverter

A Spring Boot application that converts XML files to PDF using Apache FOP. It supports automated PDF generation via directory monitoring and a REST API for file uploads.

## Features
- Convert XML to PDF with customizable XSL-FO templates.
- REST API for on-demand PDF generation.
- Directory monitoring for automatic XML-to-PDF conversion.
- Flexible handling of diverse XML structures.

## Getting Started
### Prerequisites
- Java 17
- Maven

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/SpringFopConverter.git

2. Build the project:
```
mvn clean package
```
3. Run the application:
```
java -jar target/SpringFopConverter-0.0.1-SNAPSHOT.jar
```
### Usage

#### Directory Monitoring
Place XML files in input/ to generate PDFs in output/.
#### REST API
Upload XML files via
```bash
curl -X POST -F "file=@sample.xml" http://localhost:8080/convert -o output.pdf
```
#### Example
Input sample.xml:
```xml
<document>
    <title>Test Document</title>
    <body>This is a test.</body>
</document>
```
Output sample.pdf:

Displays the XML structure with a logo and bold tag names.
### Contributing
Pull requests are welcome! For major changes, please open an issue first.

### License
MIT License