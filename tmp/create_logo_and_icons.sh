#!/usr/bin/env bash
set -e

SRC="app/src/main/res/drawable/krushi_sahayak_logo.png"

# Generate 512x512 high-resolution Krushi Sahayak logo PNG
convert -size 512x512 xc:"#1B5E20" \
  -fill "#2E7D32" -draw "circle 256,256 256,30" \
  -fill none -stroke "#FFD54F" -strokewidth 4 -draw "circle 256,256 256,50" \
  -fill "#FFD54F" -stroke none -draw "circle 256,180 256,110" \
  -fill "#FFE082" -draw "circle 256,180 256,135" \
  -fill "#81C784" -draw "path 'M 130,350 C 130,270 200,230 256,260 C 220,300 200,350 220,390 C 170,390 130,380 130,350 Z'" \
  -fill "#81C784" -draw "path 'M 382,350 C 382,270 312,230 256,260 C 292,300 312,350 292,390 C 342,390 382,380 382,350 Z'" \
  -fill "#A5D6A7" -draw "path 'M 256,330 C 210,280 160,240 185,175 C 235,190 255,250 256,330 Z'" \
  -fill "#A5D6A7" -draw "path 'M 256,330 C 302,280 352,240 327,175 C 277,190 257,250 256,330 Z'" \
  -fill "#FFCA28" -draw "ellipse 256,110 14,24 0,360" \
  -fill "#FFD54F" -draw "ellipse 242,135 12,20 0,360" \
  -fill "#FFD54F" -draw "ellipse 270,135 12,20 0,360" \
  -fill "#FFD54F" -draw "ellipse 240,165 12,20 0,360" \
  -fill "#FFD54F" -draw "ellipse 272,165 12,20 0,360" \
  -stroke "#FFF9C4" -strokewidth 5 -draw "line 256,390 256,120" \
  -stroke none \
  -fill "#FFFFFF" -draw "circle 380,380 380,335" \
  -fill "#1B5E20" -draw "circle 380,380 380,342" \
  -fill "#FFD54F" -draw "roundrectangle 373,355 387,405 4,4" \
  -fill "#FFD54F" -draw "roundrectangle 355,373 405,387 4,4" \
  "PNG32:$SRC"

echo "Generated $SRC successfully"

# Generate mipmaps for all densities
BASE="app/src/main/res"
for spec in mdpi:48 hdpi:72 xhdpi:96 xxhdpi:144 xxxhdpi:192; do
  dir="$BASE/mipmap-${spec%:*}"
  size="${spec#*:}"
  rm -f "$dir/ic_launcher.webp" "$dir/ic_launcher_round.webp"
  convert "$SRC" -resize "${size}x${size}!" "PNG32:$dir/ic_launcher.png"
  radius=$((size / 2))
  convert "$SRC" -resize "${size}x${size}!" \
    \( -size "${size}x${size}" xc:none -fill white \
       -draw "circle $radius,$radius $radius,0" \) \
    -alpha set -compose DstIn -composite "PNG32:$dir/ic_launcher_round.png"
  echo "Generated $dir/ic_launcher.png ($size x $size)"
done

echo "All launcher icons generated successfully!"
