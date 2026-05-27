pipeline {
    agent any

    stages {

        stage('Check project files') {
            steps {
                sh '''
                echo "Vérification de la structure du projet"

                test -f docker-compose.yml
                test -f dags/ingest_patients_from_minio.py
                test -f postgres-init/01_init_hospital.sql
                test -d data/csv

                echo "Structure OK"
                '''
            }
        }

        stage('Check DAG syntax') {
            steps {
                sh '''
                echo "Vérification simple du DAG Airflow"

                grep -q "dag_id=\"ingest_patients_from_minio\"" dags/ingest_patients_from_minio.py
                grep -q "MINIO_BUCKET" dags/ingest_patients_from_minio.py
                grep -q "patient" postgres-init/01_init_hospital.sql

                echo "DAG OK"
                '''
            }
        }

        stage('Trigger Airflow DAG') {
            steps {
                sh '''
                echo "Déclenchement du DAG Airflow"

                curl -X POST "http://demo_airflow:8080/api/v1/dags/ingest_patients_from_minio/dagRuns" \
                  -H "Content-Type: application/json" \
                  --user "admin:admin" \
                  -d "{\\"dag_run_id\\": \\"jenkins_run_${BUILD_NUMBER}\\"}"

                echo "DAG déclenché"
                '''
            }
        }

        stage('Wait') {
            steps {
                sh '''
                echo "Attente de l'exécution Airflow"
                sleep 20
                '''
            }
        }

        stage('Check PostgreSQL data') {
            steps {
                sh '''
                echo "Contrôle des données insérées"

                docker exec demo_postgres_hospital psql -U hospital_user -d hospital_db -c "SELECT COUNT(*) FROM patient;"
                docker exec demo_postgres_hospital psql -U hospital_user -d hospital_db -c "SELECT COUNT(*) FROM service;"

                echo "Contrôle terminé"
                '''
            }
        }
    }
}