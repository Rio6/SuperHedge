The map files are in /assets/maps

It is in JSON format, root array must be "entities"

Under "entities", you can add some entity data in it
The first entity must be hedgehog (type = 0)

datas are different for different entity

	Entity		Type	Data
-------------------------------
	hedgehog	0		[facing way](0=left, 2=right, 1 and 3 are no effected)
	portal		1		none
	apple		2		none
	dog			3		same as hedgehog
	wall		4		[width of wall, height of wall]