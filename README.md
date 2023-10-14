# @kduma-autoid/capacitor-usb-scale

Capacitor adapter for cheap USB scales like Dymo M10

## Install

```bash
npm install @kduma-autoid/capacitor-usb-scale
npx cap sync
```

## API

<docgen-index>

* [`enumerateDevices()`](#enumeratedevices)
* [`requestPermission(...)`](#requestpermission)
* [`hasPermission(...)`](#haspermission)
* [`open(...)`](#open)
* [`close()`](#close)
* [`addListener('onRead', ...)`](#addlisteneronread)
* [`addListener('onScaleConnected', ...)`](#addlisteneronscaleconnected)
* [`addListener('onScaleDisconnected', ...)`](#addlisteneronscaledisconnected)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)
* [Enums](#enums)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### enumerateDevices()

```typescript
enumerateDevices() => Promise<EnumerateDevicesResponse>
```

Get a list of all connected compatible USB scale devices

**Returns:** <code>Promise&lt;<a href="#enumeratedevicesresponse">EnumerateDevicesResponse</a>&gt;</code>

--------------------


### requestPermission(...)

```typescript
requestPermission(options?: RequestPermissionOptions | undefined) => Promise<void>
```

Request permission to access the USB scale device

Throws an error if permission is denied

| Param         | Type                                                                          |
| ------------- | ----------------------------------------------------------------------------- |
| **`options`** | <code><a href="#requestpermissionoptions">RequestPermissionOptions</a></code> |

--------------------


### hasPermission(...)

```typescript
hasPermission(options?: HasPermissionOptions | undefined) => Promise<HasPermissionResponse>
```

Check if app has permission to access the USB scale device

| Param         | Type                                                                  |
| ------------- | --------------------------------------------------------------------- |
| **`options`** | <code><a href="#haspermissionoptions">HasPermissionOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#haspermissionresponse">HasPermissionResponse</a>&gt;</code>

--------------------


### open(...)

```typescript
open(options?: OpenOptions | undefined) => Promise<void>
```

Open the USB scale device for data reading

Throws an error if the device is not connected or permission is denied

| Param         | Type                                                |
| ------------- | --------------------------------------------------- |
| **`options`** | <code><a href="#openoptions">OpenOptions</a></code> |

--------------------


### close()

```typescript
close() => Promise<void>
```

Close the USB scale device

--------------------


### addListener('onRead', ...)

```typescript
addListener(eventName: 'onRead', listenerFunc: (event: OnReadEvent) => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

Event emitted when the scale sends data

| Param              | Type                                                                    |
| ------------------ | ----------------------------------------------------------------------- |
| **`eventName`**    | <code>'onRead'</code>                                                   |
| **`listenerFunc`** | <code>(event: <a href="#onreadevent">OnReadEvent</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### addListener('onScaleConnected', ...)

```typescript
addListener(eventName: 'onScaleConnected', listenerFunc: (event: OnScaleConnectedEvent) => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

Event emitted when a compatible USB scale device is connected.

| Param              | Type                                                                                        |
| ------------------ | ------------------------------------------------------------------------------------------- |
| **`eventName`**    | <code>'onScaleConnected'</code>                                                             |
| **`listenerFunc`** | <code>(event: <a href="#onscaleconnectedevent">OnScaleConnectedEvent</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### addListener('onScaleDisconnected', ...)

```typescript
addListener(eventName: 'onScaleDisconnected', listenerFunc: (event: OnScaleDisconnectedEvent) => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

Event emitted when a compatible USB scale device is disconnected.

| Param              | Type                                                                                              |
| ------------------ | ------------------------------------------------------------------------------------------------- |
| **`eventName`**    | <code>'onScaleDisconnected'</code>                                                                |
| **`listenerFunc`** | <code>(event: <a href="#onscaledisconnectedevent">OnScaleDisconnectedEvent</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => Promise<void>
```

Removes all listeners

--------------------


### Interfaces


#### EnumerateDevicesResponse

| Prop          | Type                     |
| ------------- | ------------------------ |
| **`devices`** | <code>USBDevice[]</code> |


#### USBDevice

| Prop          | Type                                                 |
| ------------- | ---------------------------------------------------- |
| **`id`**      | <code>string</code>                                  |
| **`vid`**     | <code>number</code>                                  |
| **`pid`**     | <code>number</code>                                  |
| **`serial`**  | <code>string</code>                                  |
| **`product`** | <code>{ manufacturer: string; name: string; }</code> |


#### RequestPermissionOptions

| Prop            | Type                | Description                                                                            |
| --------------- | ------------------- | -------------------------------------------------------------------------------------- |
| **`device_id`** | <code>string</code> | The device to request permission for. If not specified, the first device will be used. |


#### HasPermissionResponse

| Prop             | Type                 | Description                                                   |
| ---------------- | -------------------- | ------------------------------------------------------------- |
| **`permission`** | <code>boolean</code> | Whether the app has permission to access the USB scale device |


#### HasPermissionOptions

| Prop            | Type                | Description                                                                          |
| --------------- | ------------------- | ------------------------------------------------------------------------------------ |
| **`device_id`** | <code>string</code> | The device to check permission for. If not specified, the first device will be used. |


#### OpenOptions

| Prop            | Type                | Description                                                          |
| --------------- | ------------------- | -------------------------------------------------------------------- |
| **`device_id`** | <code>string</code> | The device to open. If not specified, the first device will be used. |


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |


#### OnReadEvent

| Prop         | Type                                                |
| ------------ | --------------------------------------------------- |
| **`data`**   | <code>number[]</code>                               |
| **`weight`** | <code>number</code>                                 |
| **`status`** | <code><a href="#scalestatus">ScaleStatus</a></code> |


#### OnScaleConnectedEvent

| Prop         | Type                                            |
| ------------ | ----------------------------------------------- |
| **`device`** | <code><a href="#usbdevice">USBDevice</a></code> |


#### OnScaleDisconnectedEvent

| Prop         | Type                                            |
| ------------ | ----------------------------------------------- |
| **`device`** | <code><a href="#usbdevice">USBDevice</a></code> |


### Enums


#### ScaleStatus

| Members               | Value                           |
| --------------------- | ------------------------------- |
| **`Fault`**           | <code>"fault"</code>            |
| **`Zero`**            | <code>"zero"</code>             |
| **`InMotion`**        | <code>"in-motion"</code>        |
| **`Stable`**          | <code>"stable"</code>           |
| **`UnderZero`**       | <code>"under-zero"</code>       |
| **`OverWeight`**      | <code>"over-weight"</code>      |
| **`NeedCalibration`** | <code>"need-calibration"</code> |
| **`NeedZeroing`**     | <code>"need-zeroing"</code>     |
| **`Unknown`**         | <code>"unknown"</code>          |

</docgen-api>

### Events

#### usb_scale_read

```typescript
addEventListener(type: "usb_scale_read", listener: (ev: ScaleRead) => any, useCapture?: boolean): void;
```

Emitted when the scale sends data, and there is no callback set by `setIncomingWeightDataCallback`.

#### usb_scale_connected

```typescript
addEventListener(type: "usb_scale_connected", listener: (ev: { device: USBDevice }) => any, useCapture?: boolean): void;
```

Emitted when a compatible USB scale device is connected.

#### usb_scale_disconnected

```typescript
addEventListener(type: "usb_scale_disconnected", listener: (ev: { device: USBDevice }) => any, useCapture?: boolean): void;
```

Emitted when a compatible USB scale device is disconnected.