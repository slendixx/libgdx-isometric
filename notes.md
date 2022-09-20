# understanding isometric coordinates

## Projection description

movement in the X and Y axes is goberned by the unit vectors **i** & **j**.  
Their components are:

        ix = 1.0 iy = 0.5
        jx = -1.0 jy = 0.5

i & j are of type Vector2.  
with these unit vectors we can apply the isometric transformation to any (x,y)
coordinate.  
To do this we perform the following calculation:

        x * i + y * j

we would obtain a new Vector2 that represents our position transformed for this projection.
