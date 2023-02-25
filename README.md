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
* [`open(...)`](#open)
* [`stop()`](#stop)
* [`setIncomingWeightDataCallback(...)`](#setincomingweightdatacallback)
* [`clearIncomingWeightDataCallback()`](#clearincomingweightdatacallback)
* [Type Aliases](#type-aliases)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### enumerateDevices()

```typescript
enumerateDevices() => Promise<{ devices: USBDevice[]; }>
```

**Returns:** <code>Promise&lt;{ devices: USBDevice[]; }&gt;</code>

--------------------


### requestPermission(...)

```typescript
requestPermission(device?: string | undefined) => Promise<{ status: boolean; }>
```

| Param        | Type                |
| ------------ | ------------------- |
| **`device`** | <code>string</code> |

**Returns:** <code>Promise&lt;{ status: boolean; }&gt;</code>

--------------------


### open(...)

```typescript
open(device?: string | undefined) => Promise<void>
```

| Param        | Type                |
| ------------ | ------------------- |
| **`device`** | <code>string</code> |

--------------------


### stop()

```typescript
stop() => Promise<void>
```

--------------------


### setIncomingWeightDataCallback(...)

```typescript
setIncomingWeightDataCallback(callback: (data: ScaleRead) => void) => Promise<CallbackID>
```

| Param          | Type                                                               |
| -------------- | ------------------------------------------------------------------ |
| **`callback`** | <code>(data: <a href="#scaleread">ScaleRead</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;string&gt;</code>

--------------------


### clearIncomingWeightDataCallback()

```typescript
clearIncomingWeightDataCallback() => Promise<void>
```

--------------------


### Type Aliases


#### USBDevice

<code>{ id: string, vid: number, pid: number, serial?: string, product: { manufacturer: string, name: string } }</code>


#### ScaleRead

<code>{ data: number[], weight: number, status: <a href="#scalestatus">ScaleStatus</a> }</code>


#### ScaleStatus

<code>"Fault" | "Zero" | "InMotion" | "Stable" | "UnderZero" | "OverWeight" | "NeedCalibration" | "NeedZeroing" | "Unknown"</code>


#### CallbackID

<code>string</code>

</docgen-api>
