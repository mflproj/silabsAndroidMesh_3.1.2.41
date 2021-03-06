/*
 * Copyright © 2019 Silicon Labs, http://www.silabs.com. All rights reserved.
 */

package com.siliconlabs.bluetoothmesh.App

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.IntentFilter
import android.location.LocationManager
import com.siliconlabs.bluetoothmesh.App.Database.DeviceFunctionalityDb
import com.siliconlabs.bluetoothmesh.App.Logic.*
import com.siliconlabs.bluetoothmesh.App.Models.MeshNetworkManager
import com.siliconlabs.bluetoothmesh.App.Models.MeshNodeManager
import com.siliconlabs.bluetoothmesh.App.Models.ProvisioningModel
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application
    }

    @Provides
    @Singleton
    fun provideMeshLogic(context: Context): MeshLogic {
        return MeshLogic(context)
    }

    @Provides
    @Singleton
    fun provideMeshNetworkManager(meshLogic: MeshLogic): MeshNetworkManager {
        return MeshNetworkManager(meshLogic)
    }

    @Provides
    @Singleton
    fun provideDeviceFunctionalityDb(context: Context): DeviceFunctionalityDb {
        return DeviceFunctionalityDb(context)
    }

    @Provides
    @Singleton
    fun provideMeshNodeManager(deviceFunctionalityDb: DeviceFunctionalityDb): MeshNodeManager {
        return MeshNodeManager(deviceFunctionalityDb)
    }

    @Provides
    @Singleton
    fun provideNetworkConnectionLogic(context: Context, meshLogic: MeshLogic, bluetoothLeScanLogic: BluetoothScanner): NetworkConnectionLogic {
        return NetworkConnectionLogic(context, meshLogic.bluetoothMesh.connectableDeviceHelper, bluetoothLeScanLogic)
    }

    @Provides
    @Singleton
    fun provideProvisioningModel(meshLogic: MeshLogic, meshNodeManager: MeshNodeManager, networkConnectionLogic: NetworkConnectionLogic): ProvisioningModel {
        return ProvisioningModel(meshLogic, meshNodeManager)
    }

    @Provides
    @Singleton
    fun provideBluetoothStateReceiver(context: Context): BluetoothStateReceiver {
        val bluetoothStateReceiver = BluetoothStateReceiver()
        val intentFilter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        context.registerReceiver(bluetoothStateReceiver, intentFilter)
        return bluetoothStateReceiver
    }

    @Provides
    @Singleton
    fun provideLocationStateReceiver(context: Context): LocationStateReceiver {
        val locationStateReceiver = LocationStateReceiver()
        val intentFilter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        context.registerReceiver(locationStateReceiver, intentFilter)
        return locationStateReceiver
    }

    @Provides
    @Singleton
    fun provideBluetoothScanner(bluetoothStateReceiver: BluetoothStateReceiver): BluetoothScanner {
        return BluetoothScanner(bluetoothStateReceiver)
    }
}
