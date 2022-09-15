# sync-scrolling
A project used to find out how to best implement synchronous scrolling between two VirtualFlow instances.

There are three demos included in this project. Their class names describe their approach to solving the issue of syncing the scrolling
between two virtual flows.

* PositionPropertyBinding - binds the positionProperty() of the two flows
* ScrollBarPropertiesBinding - binds several properties of the two vertical scrollbars found in the virtual flows
* ScrollToMethods - uses a computational approach and the scrollTo() method to adjust the left-hand side after changes made to the right-hand side
