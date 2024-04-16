import mediafile
import io
from flask import Flask, request, jsonify, Response
from werkzeug.utils import secure_filename
import openai

# API key and pricing configuration (consider environment variables)
openai.api_key = "YOUR_OPENAI_API_KEY"
openai_price_per_minute = 0.006
your_fee_per_minute = openai_price_per_minute * 0.25  # 25% fee
average_speaking_rate = 150

# Allowed file extensions
ALLOWED_EXTENSIONS = {'mp3', 'wav', 'ogg', 'mp4', 'mov'}

app = Flask(__name__)


@app.route('/upload', methods=['POST'])
def upload_file():
    """Uploads a file, transcribes it with Whisper, estimates cost, and returns the transcript as a downloadable text file."""

    if 'file' not in request.files:
        return jsonify({'error': 'No file uploaded'}), 400

    uploaded_file = request.files['file']
    filename = secure_filename(uploaded_file.filename)

    if filename.rsplit('.', 1)[1].lower() not in ALLOWED_EXTENSIONS:
        return jsonify({'error': 'Invalid file format'}), 400

    try:
        file_content = uploaded_file.read()
        transcript_file = transcribe_with_whisper(file_content)

        # Estimate duration (consider moving this to a separate function for clarity)
        estimated_duration_minutes = estimate_transcript_duration(file_content)
        if estimated_duration_minutes is not None:
            total_cost = calculate_total_cost(estimated_duration_minutes)
            print(f"Estimated transcript duration: {estimated_duration_minutes:.2f} minutes")
            print(f"Estimated total cost: ${total_cost:.2f}")

        response = Response(
            transcript_file.read(),
            mimetype="text/plain",
            headers={"Content-Disposition": "attachment;filename=transcript.txt"}
        )
        return response

    except Exception as e:
        return jsonify({'error': f'Error during processing: {str(e)}'}), 500

    return jsonify({'message': 'File uploaded and transcribed successfully'}), 200


def transcribe_with_whisper(file_content):
    """Transcribes the audio/video file content using Whisper and returns a formatted text file object."""

    uploaded_file_obj = openai.File.create(content=file_content)
    response = openai.Completion.create(
        engine="text-davinci-003",
        file=uploaded_file_obj.id,
        prompt="Transcribe the audio/video file",
        max_tokens=1500,
        n=1,
        stop=None,
    )
    transcript = response.choices[0].text.strip()

    formatted_transcript = "\n".join(transcript.splitlines())  # Format transcript

    text_file = io.StringIO()
    text_file.write(formatted_transcript)
    text_file.seek(0)  # Rewind for reading

    return text_file


def calculate_total_cost(transcript_duration_minutes):
    """Calculates the total cost based on duration and fees."""

    total_cost = transcript_duration_minutes * (openai_price_per_minute + your_fee_per_minute)
    return total_cost


def estimate_transcript_duration(file_content):
    """Estimates transcript duration using file properties (if available) or fallback method."""

    try:
        # Try extracting duration from file properties (more accurate)
        with open(io.BytesIO(file_content), 'rb') as f:
            audio_properties = mediafile.read(f)
            duration_seconds = audio_properties.duration
            duration_minutes = duration_seconds / 60
            return duration_minutes
    except Exception as e:
        print(f"Error extracting duration: {e}")
        # Fallback: estimate duration from transcript length (less accurate)
        transcript = transcribe_with_whisper(file_content)  # Assuming this returns the transcript text
        return estimate_transcript_duration_from_transcript(transcript)


def estimate_transcript_duration_from_transcript(transcript):
  # Assuming an average speaking rate of 150 words per minute
  word_count = len(transcript.split())
  estimated_duration_minutes = word_count / average_speaking_rate
  return estimated_duration_minutes
