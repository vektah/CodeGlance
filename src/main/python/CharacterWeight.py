import Image,ImageDraw,ImageFont,sys

# Generates java source for the static character weight map in CharacterWeight.java by
# printing each char and observing the amount of black vs white present in the image.



def getWeight(char,font):
	boostFactor = 2.0
	image = Image.new("RGBA", (7, 12), (255,255,255))

	draw = ImageDraw.Draw(image)

	draw.text((0,0), char, (0, 0, 0), font=font)

	pix = image.load()

	topAverage = 0
	count = 0
	for x in xrange(0, 7):
		for y in xrange(0, 6):
			topAverage += pix[x,y][0] / 255.0
			count += 1
	topAverage /= count

	bottomAverage = 0
	count = 0
	for x in xrange(0, 7):
		for y in xrange(7, 12):
			bottomAverage += pix[x,y][0] / 255.0
			count += 1

	bottomAverage /= count

	return (1 - topAverage) * boostFactor, (1 - bottomAverage) * boostFactor

font = ImageFont.truetype("./cour.ttf", 12)

print "{"
for char in xrange(33, 127):
	top, bottom = getWeight(chr(char), font);
	print "\t%2.4ff,\t// %03d = '%s' (top)" % (top, char, chr(char))
	print "\t%2.4ff,\t// %03d = '%s' (bottom)" % (bottom, char, chr(char))
print "};"
