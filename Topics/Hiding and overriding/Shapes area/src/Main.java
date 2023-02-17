class Shape {
    public double area() {
        return 0;
    }
}

class Triangle extends Shape {
    double height;
    double base;

    // override the method here
    @Override
    public double area() {
        return (this.base * this.height) / 2;
    }
}

class Circle extends Shape {
    double radius;

    // override the method here
    @Override
    public double area() {
        return Math.PI * Math.pow(this.radius, 2);
    }
}

class Square extends Shape {
    double side;

    // override the method here
    @Override
    public double area() {
        return Math.pow(this.side, 2);
    }
}

class Rectangle extends Shape {
    double width;
    double height;

    // override the method here
    @Override
    public double area() {
        return this.width * this.height;
    }
}