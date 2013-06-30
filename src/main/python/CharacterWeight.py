import Image,ImageDraw,ImageFont,sys

# Generates java source for the static character weight map in CharacterWeight.java by
# printing each char and observing the amount of black vs white present in the image.

def getWeight(char,font):
	image = Image.new("RGBA", (7, 12), (255,255,255))

	draw = ImageDraw.Draw(image)

	draw.text((0,0), char, (0, 0, 0), font=font)

	pix = image.load()

	average = 0
	count = 0
	for x in xrange(0, 7):
		for y in xrange(0, 12):
			average += pix[x,y][0] / 255.0
			count += 1

	return (1 - (average / count)) * 2.4  # multiply by two is not accurate, but it does boost the legibility of the minimap...

font = ImageFont.truetype("/usr/share/fonts/truetype/ubuntu-font-family/UbuntuMono-B.ttf", 12)

print "{"
for char in xrange(33, 127):
	print "\t%2.4ff,\t// %03d = '%s'" % (getWeight(chr(char), font), char, chr(char))
print "};"
