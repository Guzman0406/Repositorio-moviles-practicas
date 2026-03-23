package com.autosync.main.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.autosync.main.data.repository.ServiceRepository
import com.autosync.main.data.repository.VehicleRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class ServiceReminderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var serviceRepository: ServiceRepository

    @Inject
    lateinit var vehicleRepository: VehicleRepository

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                checkUpcomingServices(context)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun checkUpcomingServices(context: Context) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val userId = currentUser.uid

        try {
            // Fetch list snapshot using first()
            val services = serviceRepository.getServicesForUser(userId).first()
            
            val today = Calendar.getInstance()
            val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }

            resetTime(today)
            resetTime(tomorrow)

            services.forEach { service ->
                service.nextServiceDate?.let { nextDate ->
                    val nextDateCal = Calendar.getInstance().apply { time = nextDate }
                    resetTime(nextDateCal)

                // Optimization: Only fetch vehicle if date matches
                    if (nextDateCal == today || nextDateCal == tomorrow) {
                        var vehicleName = "tu vehículo"
                        try {
                            val vehicle = vehicleRepository.getVehicleById(service.vehicleId)
                            vehicle?.let { vehicleName = it.make } 
                        } catch(e: Exception) {
                           // Keep default name if fetch fails
                        }
                        
                        if (nextDateCal == today) {
                            NotificationHelper.showNotification(
                                context,
                                "Servicio Hoy",
                                "Hoy es el día del servicio programado para $vehicleName."
                            )
                        } else if (nextDateCal == tomorrow) {
                            NotificationHelper.showNotification(
                                context,
                                "Servicio Mañana",
                                "Recuerda que mañana tienes servicio para $vehicleName."
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun resetTime(cal: Calendar) {
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
    }
}
