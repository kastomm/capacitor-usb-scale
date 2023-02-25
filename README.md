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

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### enumerateDevices()

```typescript
enumerateDevices() => Promise<{ devices: { id: string; vid: number; pid: number; serial?: string; product: { manufacturer: string; name: string; }; }[]; }>
```

**Returns:** <code>Promise&lt;{ devices: { id: string; vid: number; pid: number; serial?: string; product: { manufacturer: string; name: string; }; }[]; }&gt;</code>

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

</docgen-api>
