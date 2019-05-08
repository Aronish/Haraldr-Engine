package main.java.physics;

class CollisionDataMap {

    private EnumDirection collisionDirection;
    private float inside;

    CollisionDataMap(){
        this.collisionDirection = EnumDirection.INVALIDDIR;
        this.inside = 0.0f;
    }

    void setData(EnumDirection collisionDirection, float inside){
        this.collisionDirection = collisionDirection;
        this.inside = inside;
    }

    EnumDirection getCollisionDirection(){
        return this.collisionDirection;
    }

    float getInside(){
        return this.inside;
    }
}
