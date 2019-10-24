#!/bin/bash
mkdir -p output
java -jar jar/TP.jar $1
dot -Tpng output/cfg.dot -o output/cfg.png
dot -Tpng output/tree.dot -o output/tree.png
dot -Tpng output/cdg.dot -o output/cdg.png
dot -Tpng output/ddg.dot -o output/ddg.png
dot -Tpng output/pdg.dot -o output/pdg.png
