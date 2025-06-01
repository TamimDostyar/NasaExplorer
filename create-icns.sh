#!/bin/bash

# Create temporary iconset directory
ICONSET="nasa-icon.iconset"
mkdir -p $ICONSET

# Convert SVG to PNG files of different sizes
for size in 16 32 64 128 256 512; do
  sips -s format png src/main/resources/nasa-icon.svg --out "$ICONSET/icon_${size}x${size}.png" -z $size $size
  if [ $size -le 256 ]; then
    sips -s format png src/main/resources/nasa-icon.svg --out "$ICONSET/icon_${size}x${size}@2x.png" -z $((size*2)) $((size*2))
  fi
done

# Create ICNS file
iconutil -c icns $ICONSET

# Move the ICNS file to resources
mv nasa-icon.icns src/main/resources/

# Clean up
rm -rf $ICONSET 