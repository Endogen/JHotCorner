JHotCorner
==========

Hot corner implementation in Java. Inspired by GNOME 3.10.

How to use
==========

Implement 'JHotCornerInterface'. That will add four methods:
```java
    @Override
    public void onTopLeftCorner() {

    }

    @Override
    public void onTopRightCorner() {

    }

    @Override
    public void onBottomLeftCorner() {

    }

    @Override
    public void onBottomRightCorner() {

    }
```
To register a corner:
JHotCorner.getInstance().registerHotCorner(JHotCorner.Corner.TOP_LEFT, this);

To show default corner image:
JHotCorner.getInstance().setShowCornerImage(true);

To set own corner image:
JHotCorner.getInstance().setCornerImageUrl(getClass().getClassLoader().getResource("examples/big.png"));

To change corner radius:
JHotCorner.getInstance().setCornerRadius(60);