# Controls

- **Arrow keys:** Move the player.
- **Escape key:** Pause and unpause.
- **E:** Open inventory.
- **Z** or **left-click:** Use held item.
- **Left-click on an inventory slot**: Move item around. Click again to place the selected item into an empty slot or swap it with another item. Click anywhere out of the inventory to drop held item.
- **Q:** Drop held item or hovered item in inventory.
- **Ctrl in the inventory**: The control key adds some functionalities to the inventory, such as quick equipping an item when left-clicking or dropping an entire stack along with Q.
- **C:** Dodge (become immune to damage for a fraction of time).
- **1-7** or **left-click an inventory bar slot:** Select slot.
- **\[** and **\]:** Test keys with functionality that varies by version; these can be customized in KeyHandler.java at the end of the updateKeys() method.

### Planning mode
*Planning is a special mode where interactions are stopped, and will only resume if canceled or executed. Once executed, the plan cannot be canceled. It will end only after successful completion or if interrupted by damage or direct collision. Damage is multiplied by a value varying by the number of steps given, and how successful is the plan.*

- **Spacebar:** Toggle planning mode.
- **Enter:** Confirm plan and execute it.
- **Arrow keys:** Move plan step. If returning, undo last step.
- **Left-click:** Confirm plan and execute it.
- **Right-click:** Undo last step.

During plan execution, most controls are halted or modified, except for pausing. Those modified are:
- **Enter, arrow keys and mouse clicks:** Skip a single step.
- **Spacebar:** Toggle quick execution.
